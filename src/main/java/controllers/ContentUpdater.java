package controllers;

import algorithm.AntAPI;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import scala.Tuple2;

import java.io.File;

/**
 * @author Wojciech Pachuta.
 */
public class ContentUpdater extends Thread implements Runnable {

    private final AntAPI antAPI;

    private final ContentController contentController;

    private final Image grayImage;
    private final int imageHeight;
    private final int imageWidth;

    private boolean paused = false;
    private final Object obj = new Object();

    public ContentUpdater(String imageName, ContentController contentController) {
        this.antAPI = new AntAPI(new File(imageName), contentController.getAlpha(), contentController.getBeta(),
                contentController.getEvaporationRate(), contentController.getGradientThreshold(),
                0.01, contentController.getNumberOfAntsMultiplier());
        this.contentController = contentController;
        Image inputImage = new Image("file:" + imageName);
        this.imageWidth = (int) inputImage.getWidth();
        this.imageHeight = (int) inputImage.getHeight();
        WritableImage writableImage = new WritableImage(imageWidth, imageHeight);
        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                writableImage.getPixelWriter().setColor(i, j, inputImage.getPixelReader().getColor(i, j).grayscale());
            }

        }
        this.grayImage = writableImage;
    }


    @Override
    public synchronized void run() {
        try {
            contentController.setImage(grayImage);

            while(true) {
                while (paused) {
                    wait();
                }
                antAPI.setAlpha(contentController.getAlpha());
                antAPI.setBeta(contentController.getBeta());
                antAPI.setEvaporationRate(contentController.getEvaporationRate());
                antAPI.setGradientThreshold(contentController.getGradientThreshold());
                antAPI.setNumberOfAntsMultiplier(contentController.getNumberOfAntsMultiplier());

                antAPI.makeSteps(contentController.getStepsPerIteration());
                WritableImage writableImage = new WritableImage(grayImage.getPixelReader(), imageWidth, imageHeight);

                for (Tuple2<Object, Object> tuple : antAPI.getStrongestPheromone(contentController.getPheromoneCoverage())) {
                    int x = (Integer) tuple._1();
                    int y = (Integer) tuple._2();
                    writableImage.getPixelWriter().setColor(x, y, Color.RED);
                }

                for (Tuple2<Object, Object> tuple : antAPI.getAnts()) {
                    int x = (Integer) tuple._1();
                    int y = (Integer) tuple._2();
                    writableImage.getPixelWriter().setColor(x, y, Color.GREEN);
                }
                contentController.setImage(writableImage);
                Thread.sleep(contentController.getMidIterationPeriod());
            }
        } catch (InterruptedException ignored) {
        }
    }

    public void pauseExecution(){
        paused = true;
    }

    public void resumeExecution(){
        if(paused) {
            synchronized (this) {
                paused = false;
                notifyAll();
            }
        }
    }
}
