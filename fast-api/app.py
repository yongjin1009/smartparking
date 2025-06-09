from fastapi import FastAPI, UploadFile, File, HTTPException
from model_detection import load_detection_model, detect_plate
from model_ocr import ocr_plate
from utils import read_imagefile, crop_box

app = FastAPI()

# Load models once at startup
detection_model = load_detection_model()

@app.post("/extract_plate/")
async def extract_plate(file: UploadFile = File(...)):
    image = read_imagefile(await file.read())

    # Detect plate
    box = detect_plate(detection_model, image)
    if not box:
        raise HTTPException(status_code=404, detail="Car plate not detected")

    # Crop plate
    cropped = crop_box(image, box)

    # OCR and return car plate number
    plate_number = ocr_plate(cropped)

    return {
        "CarPlate": plate_number,
        "box": box
    }