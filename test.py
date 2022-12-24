import numpy as np
from sklearn.metrics import confusion_matrix
from scipy.spatial.distance import cdist
from skimage.measure import label, regionprops, moments, moments_central, moments_normalized, moments_hu
from skimage import io, exposure
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle
import pickle
import train

# Original Image
img = io.imread('test.bmp')
io.imshow(img)
plt.title('Original Image')
io.show()
hist = exposure.histogram(img)
plt.bar(hist[1], hist[0])
plt.title('Histogram')
plt.show()

# Binary Image
th = 200
img_binary = (img < th).astype(np.double)
io.imshow(img_binary)
plt.title('Binary Image')
io.show()

# Extracting Characters and Their Features
# Image Label
img_label = label(img_binary, background=0)
io.imshow(img_label)
plt.title('Labeled Image')
io.show()
#print(np.amax(img_label))

# Bounding boxes
Features = []
bboxes = []
regions = regionprops(img_label)
io.imshow(img_binary)
ax = plt.gca()
for props in regions:
    minr, minc, maxr, maxc = props.bbox
    # Hu moments and removing small components
    roi = img_binary[minr:maxr, minc:maxc]
    m = moments(roi)
    cc = m[0, 1] / m[0, 0]
    cr = m[1, 0] / m[0, 0]
    mu = moments_central(roi, center=(cr, cc))
    nu = moments_normalized(mu)
    hu = moments_hu(nu)
    if maxc - minc > 10 and maxr - minr > 10:
        Features.append(hu)
        bboxes.append([minr, minc, maxr, maxc])
    ax.add_patch(Rectangle((minc, minr), maxc - minc, maxr - minr, fill=False, edgecolor='red', linewidth=1))
io.show()

# From train.py 
files = ['a.bmp', 'd.bmp', 'm.bmp', 'n.bmp', 'o.bmp', 'p.bmp', 'q.bmp', 'r.bmp', 'u.bmp', 'w.bmp']
train_img = train.readImages(files)
train_img_binary = train.binaryImages(train_img)
train_img_label = train.labelImages(train_img_binary)
train_Features, train_Labels = train.extract_features(train_img_label, train_img_binary)

# Find the mean and standarad deviation of each column of train
train_means = train.getMean(train_Features)
train_stds = train.getStd(train_Features, train_means)

# Normalize test Features using train's means and stds
normalizedFeatures = train.normalize(Features, train_means, train_stds)
ax.set_title('Bounding Boxes')

#Also normalize train's features
normalizedTrain = train.normalize(train_Features, train_means, train_stds)

# Distance Matrix and predictions
D = cdist(normalizedFeatures, normalizedTrain)
io.imshow(D)
plt.title('Distance Matrix')
io.show()
D_index = np.argsort(D, axis=1)
first_column_indexes = D_index[:,0]
Ypred = []
for i in first_column_indexes:
    Ypred.append(train_Labels[i])

# Load ground truth locations and classes
pkl_file = open('test_gt_py3.pkl', 'rb')
mydict = pickle.load(pkl_file)
pkl_file.close()
classes = mydict[b'classes'] # n x 1
locations = mydict[b'locations'] # n x 2

# Calculation for recognition rate
correct = 0
total = len(classes)
for location, labels in zip(locations, classes):
    x, y = location
    for bbox, prediction in zip(bboxes, Ypred):
        minr, minc, maxr, maxc = bbox
        if(x>=minc and x<=maxc and y>=minr and y<=maxr and labels == prediction):
            correct+=1

recognition_rate = correct/total
print("Number of components in test image: " + str(len(bboxes)))
print("Test recognition rate: " + str(recognition_rate))

plt.figure(figsize = (8,8), dpi = 120)
plt.imshow(img_label)
plt.colorbar()
ax = plt.gca()
for bbox, pred_label in zip(bboxes, Ypred):
    min_y, min_x, max_y, max_x = bbox
    ax.add_patch(Rectangle((min_x,min_y), max_x - min_x, max_y - min_y, fill=False, edgecolor='red', linewidth=1))
    text_x = max_x
    text_y = max_y
    ax.annotate(pred_label, xy = (text_x, text_y), xycoords='data', color='green')
ax.set_title('Bounding boxes and Recognition result')
io.show()