#!/bin/bash

ECR_ID="101180582726"
ZONE="eu-south-1"
COMPONENT=document-composer
VERSION=0.0.1

docker build . -t $ECR_ID.dkr.ecr.$ZONE.amazonaws.com/$COMPONENT:$VERSION

docker login -u AWS -p $(aws ecr get-login-password --region eu-south-1) $ECR_ID.dkr.ecr.$ZONE.amazonaws.com/$COMPONENT:$VERSION

docker push $ECR_ID.dkr.ecr.$ZONE.amazonaws.com/$COMPONENT:$VERSION

