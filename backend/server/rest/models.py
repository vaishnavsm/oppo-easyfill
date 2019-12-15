from django.db import models
from django.contrib.auth.models import User
import json

class Application(models.Model):
    name = models.TextField(pk=True)
    fields = models.ManyToManyField(ApplicationField)

class ApplicationField(models.Model):
    data_name = models.TextField(pk=True)

class RequestApplication(models.Model):
    application = models.ForeignKey(Application, on_delete=models.CASCADE)
    pkey = models.TextField()
    pubkey = models.TextField()
    challenge = models.IntegerField()

class Submission(models.Model):
    application = models.ForeignKey(RequestApplication, on_delete=models.SET_NULL, null = True)
    data = models.TextField()