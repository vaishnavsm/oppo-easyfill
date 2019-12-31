from django.urls import path
from . import views
urlpatterns = [
    path('login/', views.login_user),
    path('get_form/', views.get_form),
    
]