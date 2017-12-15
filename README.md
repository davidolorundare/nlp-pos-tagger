# Natural Language Processing: Parts Of Speech Tagger
Academic Project - An NLP Parts Of Speech (PoS) Tagger program

## Overview:

This command-line program implemented is a Hidden Markov Model based Parts of Speech (PoS) tagging system which tags a given set of input of English words with their corresponding part of speech. Internally it makes use of a Viterbi decoding algorithm via a bigram tag (or state) model.

It makes use of training data obtained from segments of the Wall Street Journal (WSJ) corpus annotated with Penn Treebank POS tags.  The training data is annotated with the correct PoS tags, and enables the program to compute the accuracy of the model's performance; using a separate test data containing unknown/new words. Returned as output to the console are the tagged data as well as some performance metrics on both training and testing data, such as:

### Traing data metrics:
- Number of sentences
- Number of unique words
- Number of Word Tokens
- Number of Part-of-Speech tags present 
- Number of Bigrams

### Testing data metrics:
- Number of word tokens computed
- Confusion Matrix Accuracy


## Screenshots:


### Program execution: the tiny-train.txt training data and tiny.test.txt testing data files on the terminal

![alt text](https://github.com/davidolorundare/image-repo/blob/master/nlp-pos-tagger-images/program-execution.png "NLP PoS program running with training and testing data files on the terminal")

---


## Usage:

The build folder, in this repository, contains all the executable files needed for running the program. Download the folder to your desktop first.

Open a terminal (or commandline shell) and navigate to the build directory. i.e. '/build/'
The format for running the program is:

>> java posTaggerMain <the_training_dataset_file> <the_testing_dataset_file> <the_output_file_to_store_results> <optional smoothing -S >

where ‘posTaggerMain’ is the name of the program, ‘the_training_dataset_file’ is the name of the text file containing the training data to be used to build the bigram tag/state models, the ‘the_testing_dataset_file’ is the name of the text file
containing the test data that will be used to evaluate the data tagged by the Hidden Markov Model (HMM) Viterbi algorithm. The ‘the_output_file_to_store_result’ is the name of the text file where the results the HMM Viterbi tagging operation (and other performance statistics) should be stored.

An additional smoothing operation on the bigram tag/state model, can be enabled by appending the following command-line switch at runtime: ‘-S’

For example, while in the 'build' directory:

 This command will run the program using the data in the ‘WSJ-train.txt’ file
to train/build the tag/state models, with Laplace (add-one) smoothing enabled, which improves the program accuracy at a slight performance cost (if the ‘–S’ switch was not present then smoothing would not be enabled) and it will use the test-data in the ‘WSJ-test.txt’ file for evaluation. The data in the WSJ-test.txt will be tagged with by HMM Viterbi algorithm. Returned to the console will be the tagged data and performance statistics of the operation. These results will also be stored in the given ‘output-WSJ-test.txt’ file.
 
```>> java postagger.core.postTaggerMain "data/trainWSJ-train.txt" "data/testWSJ-test.txt" "data/output/output-WSJ-test.txt" -S```
 

This command will run the program on the tiny-train input file ‘tiny-train.txt’ without Laplace smoothing, using the test-data in the tiny-test file 'tiny-test.txt' for evaluation. Returned to the console will be the tagged data and performance statistics of the operation. These results will also be stored in the given ‘output-POS.txt’ file.

```>> java postagger.core.postTaggerMain "data/train/tiny-train.txt" "data/test/tiny-test.txt" "data/output/output-POS.txt"```
 

---

## Running Demo:

![alt text](https://media.giphy.com/media/3o6nUMFUjdAVEG6LLO/giphy.gif "program running in terminal")

---
