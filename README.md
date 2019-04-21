#Implementation of the $1 recognizer and an extension

##Introduction
This extension to the $1 recognizer algorithm minimizes the runtime of the algorithm by eliminating the need to run through every gesture's training samples for training sample size > 1. If a gesture is seen to be too unlikely to be the correct one based on defined thresholds, the algorithm skips over the remaining training samples in the set and moves on to the next gesture testing set.

##Description
Three different threshold implementations are included in this extension as well as the base algorithm. RecognizerAlgorithmBase is the base algorithm. RecognizerAlgorithmFlatCutoff implements a flat cutoff where any gesture that scores below a .7 on the first sample of its training set is skipped. RecognizerAlgorithmFlatDifference implements a threshold based on the previous best scoring gesture. If the currently tested gesture's score is over .2 below the current best, it is skipped. RecognizerAlgorithmPercentageDifference acts in the same way RecognizerAlgorithmFlatDifference does, except it uses a percentage based differential of .8 to exclude gestures.

The tests are run on their correspondingly named testing files. A stopwatch class is invoked in each test to log how long each test takes. The algorithms runtime was tested using the random-100 method described in the $1 recognizer paper (available http://faculty.washington.edu/wobbrock/pubs/uist-07.01.pdf)

All three thresholds performed similarly with marked increases in runtime and little to no sacrifices to accuracy.

##Demo
A demo is included. The demo uses a predefined training set of size 9 and runs the 3 threshold implementations and the base algorithm. In order to run the demo, open Demo.java and begin drawing. Results will be shown in the GUI that loads and will reset every time you start drawing a new gesture.