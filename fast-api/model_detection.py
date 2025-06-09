import torch
from torchvision.models.detection import fasterrcnn_resnet50_fpn
from torchvision.transforms import functional as F

from PIL import Image

def load_detection_model(path="models/frcnn.pth", num_classes=2):
    model = fasterrcnn_resnet50_fpn(pretrained=False, num_classes=num_classes)
    model.load_state_dict(torch.load(path, map_location="cpu"))
    model.eval()
    return model

def detect_plate(model, image, score_threshold=0.7):
    image_tensor = F.to_tensor(image)
    with torch.no_grad():
       outputs = model([image_tensor])[0]
    
    # Get highest scoring box above threshold
    boxes = outputs['boxes']
    scores = outputs['scores']

    # Filter indices where score > threshold
    high_score_idxs = (scores > score_threshold).nonzero(as_tuple=True)[0]

    if len(high_score_idxs) == 0:
        return None

    # Among those, get the index of the highest score
    best_idx = high_score_idxs[scores[high_score_idxs].argmax()]

    # Return the bounding box as a list
    return boxes[best_idx].tolist()
