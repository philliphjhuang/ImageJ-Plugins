import numpy as np
from sklearn.metrics import confusion_matrix
from scipy.spatial.distance import cdist
from skimage.measure import label, regionprops, moments, moments_central, moments_normalized, moments_hu
from skimage import io, exposure
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle
import pickle
import math
import seaborn as sns

# Original Images
def readImages(fileNames):
    l = []
    for i in fileNames:
        l.append(io.imread(i))
    return l
files = ['a.bmp', 'd.bmp', 'm.bmp', 'n.bmp', 'o.bmp', 'p.bmp', 'q.bmp', 'r.bmp', 'u.bmp', 'w.bmp']
#img = readImages(files)
#for i in img:
    #io.imshow(i)
    #plt.title('Original Images')
    #io.show()

# Binary Images
def binaryImages(originalImages, th=200):
    l = []
    for i in originalImages:
        l.append((i < th).astype(np.double))
    return l
#img_binary = binaryImages(img)

#for i in img_binary:
    #io.imshow(i)
    #plt.title('Binary Image')
    #io.show()

# Extracting Characters and Their Features
# Image Label
def labelImages(binaries):
    l = []
    for i in binaries:
        l.append(label(i, background=0))
    return l
#img_label = labelImages(img_binary)
#for i in img_label:
    #io.imshow(i)
    #plt.title('Labeled Image')
    #io.show()
    #print(np.amax(i))

# Bounding boxes
bboxes = []
def extract_features(labels, binaries):
    F_train = []
    L_train = []
    for i in range(len(labels)):
        bbox = []
        regions = regionprops(labels[i])
        #io.imshow(binaries[i])
        #ax = plt.gca()
        for props in regions:
            minr, minc, maxr, maxc = props.bbox
            # Hu moments and removing small components
            roi = binaries[i][minr:maxr, minc:maxc]
            m = moments(roi)
            cc = m[0, 1] / m[0, 0]
            cr = m[1, 0] / m[0, 0]
            mu = moments_central(roi, center=(cr, cc))
            nu = moments_normalized(mu)
            hu = moments_hu(nu)
            if maxc - minc > 11 and maxr - minr > 9:
                F_train.append(hu)
                L_train.append(files[i][0])
                bbox.append([minr, minc, maxr, maxc])
        bboxes.append(bbox)
            #ax.add_patch(Rectangle((minc, minr), maxc - minc, maxr - minr, fill=False, edgecolor='red', linewidth=1))
            #ax.set_title('Bounding Boxes')
        #io.show()
    return F_train, L_train
#Features, Labels = extract_features(img_label, img_binary)

# returns a list that consists a mean for each column
def getMean(Matrix):
    meansOfColumns = [0]*len(Matrix[0])
    for col in range(len(Matrix[0])):
        for row in range(len(Matrix)):
            meansOfColumns[col] += Matrix[row][col]
    for col in range(len(meansOfColumns)):
        meansOfColumns[col] /= len(Matrix)
    return meansOfColumns
#means = getMean(Features)

# returns a list that consists a std for each column
def getStd(Matrix, meansList):
    stdOfColumns = [0]*len(Matrix[0])
    for col in range(len(Matrix[0])):
        for row in range(len(Matrix)):
            stdOfColumns[col] += abs(Matrix[row][col] - meansList[col])**2
    for col in range(len(stdOfColumns)):
        stdOfColumns[col] /= len(Matrix)
        stdOfColumns[col] = math.sqrt(stdOfColumns[col])
    return stdOfColumns
#stds = getStd(Features, means)

# returns a normalized matrix by normalizing each column
def normalize(Matrix, meansList, stdList):
    normalizedMatrix = Matrix
    for r in range(len(Matrix)):
        for c in range(len(Matrix[r])):
            normalizedMatrix[r][c] = (Matrix[r][c] - meansList[c])/stdList[c]
    return normalizedMatrix
#normalizedFeatures = normalize(Features, means, stds)
""""
print(means)
print(stds)
means = getMean(normalizedFeatures)
stds = getStd(normalizedFeatures, means)
print(means)
print(stds)
"""
# Distance Matrix
def distanceMatrix(normalzied_Feature):
    distanceMatrix = cdist(normalzied_Feature, normalzied_Feature)
    io.imshow(distanceMatrix)
    plt.title('Distance Matrix')
    io.show()
    return distanceMatrix
#D = distanceMatrix(normalizedFeatures)

# Confusion Matrix
def confusionMatrix(distanceMatrix, labelList):
    D_index = np.argsort(distanceMatrix, axis=1)
    first_column_index = D_index[:,0]
    second_column_index = D_index[:,1]
    Ytrue = []
    for i in first_column_index:
        Ytrue.append(labelList[i])
    Ypred = []
    for i in second_column_index:
        Ypred.append(labelList[i])
    confM = confusion_matrix(Ytrue, Ypred)
    ax = plt.subplot()
    sns.heatmap(confM, annot=True, fmt='g', ax=ax)
    ax.set_xlabel('Predicted labels')
    ax.set_ylabel('True labels')
    ax.set_title('Confusion Matrix')
    plt.show()
    return Ytrue, Ypred
#Y_true, Y_pred = confusionMatrix(D, Labels)

#Recognition rate
def recognitionRate(Ytrue, Ypred):
    correct = 0
    total = len(Ytrue)
    for true, pred in zip(Ytrue, Ypred):
        if true==pred:
            correct+=1
    recognition_rate = correct / total
    print("Training recognition rate: " + str(recognition_rate))
#recognitionRate(Y_true, Y_pred)

#Recognition result and bounding boxes
def recognition_result(imageLabel, bounding_boxes_list, Ypred): 
    plt.figure(figsize = (8,8), dpi = 120)
    plt.imshow(imageLabel)
    plt.colorbar()
    ax = plt.gca()
    for bbox, pred_label in zip(bounding_boxes_list, Ypred):
        min_y, min_x, max_y, max_x = bbox
        ax.add_patch(Rectangle((min_x,min_y), max_x - min_x, max_y - min_y, fill=False, edgecolor='red', linewidth=1))
        text_x = max_x
        text_y = max_y
        ax.annotate(pred_label, xy = (text_x, text_y), xycoords='data', color='green')
    ax.set_title('Bounding boxes and Recognition result')
    io.show()
#for i in range(len(img_label)):
    #recognition_result(img_label[i], bboxes[i], Y_pred)