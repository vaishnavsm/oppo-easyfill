from django.db import models
from django.contrib.auth.models import User
import json


class ApplicationField(models.Model):
    data_name = models.TextField(primary_key=True)
    is_personal = models.BooleanField(default=False)
    is_required = models.BooleanField(default=True)
    engine = models.TextField(default="", blank=True, null=True)

class Application(models.Model):
    name = models.TextField(primary_key=True)
    params = models.TextField("""{"max":1201, "min":21123}""")
    fields = models.ManyToManyField(ApplicationField)

class RequestApplication(models.Model):
    application = models.ForeignKey(Application, on_delete=models.CASCADE)
    pkey = models.TextField()
    pubkey = models.TextField()
    challenge = models.IntegerField()

class Submission(models.Model):
    application = models.ForeignKey(RequestApplication, on_delete=models.SET_NULL, null = True)
    data = models.TextField()