from django.contrib import admin
from . import models
# Register your models here.
admin.site.register(models.UserClass)
admin.site.register(models.LogEntry)
admin.site.register(models.Template)