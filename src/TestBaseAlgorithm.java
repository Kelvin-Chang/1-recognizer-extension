import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TestBaseAlgorithm {
    public static void main(String[] args) {
        String user = "";
        String gestureType = "";
        ArrayList<ArrayList<ReturnValues>> userData = new ArrayList<>();

        // accuracy per user and based on number of training samples, will most likely remove in the future and use log file to calculate statistics
        int[] perUserAccuracy = new int[10];
        int[] numberTrainingSamplesAccuracy = new int[9];

        // create log file if it doesnt exist, erase log file if it does exist
        try {
            File file = new File("logfileBase.csv");
            if (!file.exists()) {
                file.createNewFile();
            } else {
                // overwrite file if it already exists
                FileOutputStream writer = new FileOutputStream("logfileBase.csv", false);
                writer.write(("").getBytes());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start write stream for logging
        try {
            FileOutputStream logger = new FileOutputStream("logfile.csv", true);
            // write description to file
            logger.write(("Recognition Log: [Kelvin Chang] // [$1 Recognizer] // [$1 recognizer dataset] // USER-DEPENDENT RANDOM-100,,,,,,,,,,,\n").getBytes());

            // write column headers
            logger.write(("User[all-users],GestureType[all-gestures-types],RandomIteration[1to100],#ofTrainingExamples[E],TotalSizeOfTrainingSet[count],TrainingSetContents[specific-gesture-instances],Candidate[specific-instance],RecoResultGestureType[what-was-recognized],CorrectIncorrect[1or0],RecoResultScore,RecoResultBestMatch[specific-instance],RecoResultNBestSorted[instance-and-score]\n").getBytes());

            // for each user
            for (int i = 2; i <= 11; i++) {
                if (i < 10) {
                    user = "s" + "0" + i;
                } else {
                    user = "s" + i;
                }

                // populate userData with all of user's gestures
                for (int j = 0; j < 16; j++) {
                    gestureType = HelperFunctions.GestureType(j);
                    userData.add(HelperFunctions.BuildGestures(user, gestureType));
                }

                // for each gesture
                for (int j = 0; j < 16; j++) {
                    // repeat test 100 times
                    for (int k = 0; k < 100; k++) {
                        // for training sample sizes 1 to 9
                        for (int l = 1; l <= 9; l++) {
                            // shuffle gestures before testing each training sample size for maximum randomness
                            // dont need to shuffle a final time (should be faster?)
                            for (int m = 0; m < userData.size(); m++) {
                                Collections.shuffle(userData.get(m));
                            }

                            // temp arraylist to store training set of size l
                            ArrayList<ArrayList<ReturnValues>> temp = new ArrayList();

                            // initialize arraylist with an arraylist for each gesture
                            for (int m = 0; m < 16; m++) {
                                temp.add(new ArrayList<>());
                            }

                            // add 'l' training samples to temp arraylist
                            for (int m = 0; m < l; m++) {
                                // for each gesture
                                for (int n = 0; n < 16; n++) {
                                    // add to the list (n gestureType, m gesture number)
                                    temp.get(n).add(userData.get(n).get(m));
                                }
                            }

                            // run test

                            // store final (index 9) gesture of gesture type that is being tested in userData as the input gesture
                            ArrayList<ReturnValues> nbestList = RecognizerAlgorithmBase.Recognize(userData.get(j).get(9), temp);

                            // small accuracy analysis here------------------------------------------------------
                            // leaving this separate from the actual logging for clarity
                            if (nbestList.get(0).gesture.equals(userData.get(j).get(9).gesture)) {
                                // user (i) starts at 2, training sample (l) starts at 1 so decrement accordingly to compensate
                                perUserAccuracy[i - 2]++;
                                numberTrainingSamplesAccuracy[l - 1]++;
                            }

                            // log data here----------------------------------------------------------------------

                            // log user (i)
                            logger.write((i + ",").getBytes());

                            // log gesture type (j)
                            logger.write((HelperFunctions.GestureType(j) + ",").getBytes());

                            // log iteration number (k + 1) (+1 because loop starts at 0)
                            logger.write((k + 1 + ",").getBytes());

                            // log number of training examples (l)
                            logger.write((l + ",").getBytes());

                            // log total size of training set (16 gestures, k + 1 samples per gesture)
                            logger.write(((k + 1) * 16 + ",").getBytes());

                            // log training set contents
                            logger.write(("\"{").getBytes());
                            for (int m = 0; m < temp.size(); m++) {
                                for (int n = 0; n < temp.get(m).size(); n++) {
                                    logger.write((i + "-" + userData.get(m).get(n).gesture + "-" + userData.get(m).get(n).gestureNumber).getBytes());

                                    if (m != temp.size() - 1 && n != temp.get(m).size() - 1) {
                                        logger.write((",").getBytes());
                                    }
                                }
                            }
                            logger.write(("}\",").getBytes());

                            // log candidate
                            logger.write((i + "-" + userData.get(j).get(9).gesture + "-" + userData.get(j).get(9).gestureNumber + ",").getBytes());

                            // log recognized gesture (first element in nbest list)
                            logger.write((nbestList.get(0).gesture + ",").getBytes());

                            // log correct (1) or incorrect (0)
                            if (nbestList.get(0).gesture.equals(userData.get(j).get(9).gesture)) {
                                logger.write((1 + ",").getBytes());
                            }
                            else {
                                logger.write((0 + ",").getBytes());
                            }

                            // log result score
                            logger.write((nbestList.get(0).score + ",").getBytes());

                            // log best match instance
                            logger.write((i + "-" + nbestList.get(0).gesture + "-" + nbestList.get(0).gestureNumber + ",").getBytes());

                            // log n best list
                            logger.write(("\"{").getBytes());
                            for (int m = 0; m < nbestList.size(); m++) {
                                logger.write((i + "-" + nbestList.get(m).gesture + "-" + nbestList.get(m).gestureNumber + "," + nbestList.get(m).score).getBytes());
                                // skip last comma
                                if (m != nbestList.size() - 1) {
                                    logger.write((",").getBytes());
                                }
                            }
                            logger.write(("}\"").getBytes());

                            // new line for end of logging this testing instance
                            logger.write(("\n").getBytes());
                        }
                    }
                }

                // clear user data after testing each user
                userData.clear();
            }

            // close logger after testing is completed
            logger.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 16 gesture types, 100 iterations, 9 training sample sizes
        for (int i = 0; i < perUserAccuracy.length; i++) {
            int userNum = i + 2;
            double tempAccuracy = 1.0 - (double) perUserAccuracy[i] / (16 * 100 * 9);
            System.out.println("User " + userNum + ": " + tempAccuracy);
        }

        // 16 gesture types, 100 iterations, 10 users
        for (int i = 0; i < numberTrainingSamplesAccuracy.length; i++) {
            int sampleNum = i + 1;
            double tempAccuracy = 1.0 - (double) numberTrainingSamplesAccuracy[i] / (16 * 100 * 10);
            System.out.println("Number of Training Samples " + sampleNum + ": " + tempAccuracy);
        }
    }
}
