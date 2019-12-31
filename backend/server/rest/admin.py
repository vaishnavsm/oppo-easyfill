from django.contrib import admin
from . import models
# Register your models here.
admin.site.register(models.Application)
admin.site.register(models.ApplicationField)
admin.site.register(models.RequestApplication)
admin.site.register(models.Submission)