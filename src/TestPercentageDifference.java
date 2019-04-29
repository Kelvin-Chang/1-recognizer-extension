import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TestPercentageDifference {
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();

        String user = "";
        String gestureType = "";
        ArrayList<ArrayList<ReturnValues>> userData = new ArrayList<>();

        // size 8 for training set sizes 2-9
        double[] timePerTrainingSamples = new double[8];

        // create log file if it doesnt exist, erase log file if it does exist
        try {
            File file = new File("logfilePercentageDifference.csv");
            if (!file.exists()) {
                file.createNewFile();
            } else {
                // overwrite file if it already exists
                FileOutputStream writer = new FileOutputStream("logfilePercentageDifference.csv", false);
                writer.write(("").getBytes());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start write stream for logging
        try {
            FileOutputStream logger = new FileOutputStream("logfilePercentageDifference.csv", true);
            // write description to file
            logger.write(("Recognition Log: [Kelvin Chang] // [$1 Recognizer] // [$1 recognizer dataset] // USER-DEPENDENT RANDOM-100,,,,,,,,,,,\n").getBytes());

            // write column headers
            logger.write(("User[all-users],GestureType[all-gestures-types],RandomIteration[1to100],#ofTrainingExamples[E],TotalSizeOfTrainingSet[count],TrainingSetContents[specific-gesture-instances],Candidate[specific-instance],RecoResultGestureType[what-was-recognized],CorrectIncorrect[1or0],RecoResultScore,RecoResultBestMatch[specific-instance],RecoResultNBestSorted[instance-and-score]\n").getBytes());

            // for each user
            for (int userID = 2; userID <= 11; userID++) {
                if (userID < 10) {
                    user = "s" + "0" + userID;
                } else {
                    user = "s" + userID;
                }

                // populate userData with all of user's gestures
                for (int gestureID = 0; gestureID < 16; gestureID++) {
                    gestureType = HelperFunctions.GestureType(gestureID);
                    userData.add(HelperFunctions.BuildGestures(user, gestureType));
                }

                // repeat test 100 times
                for (int iteration = 0; iteration < 100; iteration++) {
                    // for training sample sizes 2 to 9
                    for (int trainingSampleSize = 2; trainingSampleSize <= 9; trainingSampleSize++) {
                        // shuffle user data for each gesture to create randomness
                        for (int l = 0; l < userData.size(); l++) {
                            Collections.shuffle(userData.get(l));
                        }

                        // temp arraylist to store training set of size k
                        ArrayList<ArrayList<ReturnValues>> trainingSet = new ArrayList();

                        // populate temp arraylist
                        // for each gesture
                        for (int gestureID = 0; gestureID < 16; gestureID++) {
                            // for training sample size of k
                            ArrayList<ReturnValues> tempGestureSet = new ArrayList<>();

                            // populate gesture arraylist with appropriate number of training samples
                            for (int trainingSampleNumber = 0; trainingSampleNumber < trainingSampleSize; trainingSampleNumber++) {
                                tempGestureSet.add(userData.get(gestureID).get(trainingSampleNumber));
                            }

                            // add gesture arraylist into training set
                            trainingSet.add(tempGestureSet);
                        }

                        // run test on each gesture for the created training set
                        for (int gestureID = 0; gestureID < 16; gestureID++) {
                            //log time-------------------
                            StopWatch tempTimer = new StopWatch();

                            // get n-best list by passing in a gesture to be tested (9th index to get final element that will never be added to the training set) and the previously created training set
                            ArrayList<ReturnValues> nbestList = RecognizerAlgorithmPercentageDifference.Recognize(userData.get(gestureID).get(9), trainingSet);

                            // log time------------------
                            timePerTrainingSamples[trainingSampleSize - 2] += tempTimer.getElapsedTime();

                            // log data here----------------------------------------------------------------------

                            // log user (i)
                            logger.write((userID + ",").getBytes());

                            // log gesture type (j)
                            logger.write((HelperFunctions.GestureType(gestureID) + ",").getBytes());

                            // log iteration number (k + 1) (+1 because loop starts at 0)
                            logger.write((iteration + 1 + ",").getBytes());

                            // log number of training examples (l)
                            logger.write((trainingSampleSize + ",").getBytes());

                            // log total size of training set
                            logger.write(((trainingSampleSize) * 16 + ",").getBytes());

                            // log training set contents
                            logger.write(("\"{").getBytes());
                            for (int m = 0; m < trainingSet.size(); m++) {
                                for (int n = 0; n < trainingSet.get(m).size(); n++) {
                                    logger.write((userID + "-" + userData.get(m).get(n).gesture + "-" + userData.get(m).get(n).gestureNumber + ",").getBytes());
                                }
                            }
                            logger.write(("}\",").getBytes());

                            // log candidate
                            logger.write((userID + "-" + userData.get(gestureID).get(9).gesture + "-" + userData.get(gestureID).get(9).gestureNumber + ",").getBytes());

                            // log recognized gesture (first element in nbest list)
                            logger.write((nbestList.get(0).gesture + ",").getBytes());

                            // log correct (1) or incorrect (0)
                            if (nbestList.get(0).gesture.equals(userData.get(gestureID).get(9).gesture)) {
                                logger.write((1 + ",").getBytes());
                            }
                            else {
                                logger.write((0 + ",").getBytes());
                            }

                            // log result score
                            logger.write((nbestList.get(0).score + ",").getBytes());

                            // log best match instance
                            logger.write((userID + "-" + nbestList.get(0).gesture + "-" + nbestList.get(0).gestureNumber + ",").getBytes());

                            // log n best list
//                            logger.write(("\"{").getBytes());
//                            for (int m = 0; m < nbestList.size(); m++) {
//                                logger.write((i + "-" + nbestList.get(m).gesture + "-" + nbestList.get(m).gestureNumber + "," + nbestList.get(m).score).getBytes());
//                                // skip last comma
//                                if (m != nbestList.size() - 1) {
//                                    logger.write((",").getBytes());
//                                }
//                            }
//                            logger.write(("}\"").getBytes());

                            for (int m = 0; m < nbestList.size(); m++) {
                                logger.write((nbestList.get(m).gesture + "," + nbestList.get(m).score).getBytes());
                                if (m != nbestList.size() - 1) {
                                    logger.write((",").getBytes());
                                }
                            }



                            // new line for end of logging this testing instance
                            logger.write(("\n").getBytes());

                        }

                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // print elapsed time
        System.out.println(stopWatch.getElapsedTime());

        for (int i = 0; i < timePerTrainingSamples.length; i++) {
            System.out.println("training sample size " + i + 2 + ": " + timePerTrainingSamples[i]);
        }
    }
}
