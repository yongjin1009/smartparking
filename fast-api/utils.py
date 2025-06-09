from PIL import Image
from io import BytesIO

def read_imagefile(file) -> Image.Image:
    image = Image.open(BytesIO(file)).convert("RGB")
    return image

def crop_box(image, box):
    left, top, right, bottom = map(int, box)
    return image.crop((left, top, right, bottom))