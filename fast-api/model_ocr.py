import easyocr
from PIL import Image
import numpy as np

reader = easyocr.Reader(['en'])

def ocr_plate(image: Image.Image) -> str:
    image_np = np.array(image)
    results = reader.readtext(image_np)

    # Return the highest confidence string
    if results:
        return results[0][1]  # text is in the second position
    return ""