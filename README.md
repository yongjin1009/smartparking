# Smart Parking Project
System to extract carplate number from image and manage the vehicle entry / exit. Dockerized for easy setup and deployment

## Prerequisites
- Docker installed on your machine

## Clone the repository
- git clone https://github.com/yongjin1009/smartparking.git
- cd smartparking

## Update .env file
- copy .env.example .env
- update the database connection in .env file

## Download car plate detection model
- https://www.kaggle.com/code/lyj1009/carplatedetection/output
- Move frcnn.pth file into smartparking/fast-api/models/frcnn.pth (ensure the names are correct)

## Run the container
- docker-compose up --build
- access the application at http://localhost:8080

## Endpoints
1. POST /api/entry Multipart: image(required), time(optional)
2. POST /api/exit  Multipart: image(required), time(optional)
   
**time is optional, default to current date time if not provided**
   
