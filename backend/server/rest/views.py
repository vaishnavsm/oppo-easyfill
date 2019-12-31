from django.http import JsonResponse
from django.contrib.auth.models import User
from . import models
import json
from django.contrib.auth import authenticate, login
# from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view
from .utils import AmountRecommendationEngine
# Create your views here.

from django.views.decorators.csrf import csrf_exempt

registered_engines = {}
registered_engines["amount"] = AmountRecommendationEngine("amount")

@csrf_exempt
@api_view(['GET'])
def get_form(request):
    formId = request.GET['form-id']
    form = None
    # form = models.Application.objects.get(name = formId)
    try: 
        form = models.Application.objects.get(name = formId)
    except e as Exception:
        return JsonResponse({}, status = 404)
    
    returnObject = {}
    global registered_engines
    for item in form.fields.all():
        suggestedValue = None
        if item.engine in registered_engines.keys():
            suggestedValue = registered_engines[item.engine].suggest(json.loads(form.params))
        returnObject[item.data_name] = ("personal" if item.is_personal else "specific", "yes" if item.is_required else "no", str(suggestedValue))
    print(returnObject)
    return JsonResponse(json.dumps(returnObject), status = 200, safe=False)
1
@csrf_exempt
def login_user(request):
    data = json.loads(request.body) 
    user = authenticate(username=data['username'], password=data['password']) 
    if user is not None:
        login(request, user)
        token, _ = Token.objects.get_or_create(user=user)
        return JsonResponse({'token': token.key}, status = 200)
    else:
        return JsonResponse({}, status = 401)

@csrf_exempt
def set_admin(request):
    if not request.user.is_authenticated:
        return JsonResponse({}, status = 401)
    data = json.loads(request.body)
    user = models.UserClass.objects.get(user__username = data['username'])
    if user.has_admin:
        return JsonResponse({}, 412)
    admin = models.UserClass.objects.get(user = request.user)
    if not admin.has_admin or not admin.dominates(user):
        return JsonResponse({}, status = 403)
    models.LogEntry.objects.create(actor = admin, action = "Promoted %s to admin"%user)
    user.has_admin = True
    user.save()
    return JsonResponse({}, status=200)

@csrf_exempt
def remove_admin(request):
    if not request.user.is_authenticated:
        return JsonResponse({}, status = 401)
    data = json.loads(request.body)
    user = models.UserClass.objects.get(user__username = data['username'])
    if not user.has_admin:
        return JsonResponse({}, 412)
    admin = models.UserClass.objects.get(user = request.user)
    if not admin.has_admin or not admin.dominates(user):
        return JsonResponse({}, status = 403)
    models.LogEntry.objects.create(actor = admin, action = "Demoted %s from admin"%user)
    user.has_admin = False
    user.save()
    return JsonResponse({}, status=200)

@csrf_exempt
@api_view(['POST'])
def set_level(request):
    print(request.user)
    if not request.user.is_authenticated:
        return JsonResponse({}, status = 401)
    data = json.loads(request.body)
    user = models.UserClass.objects.get(user__username = data['username'])
    admin = models.UserClass.objects.get(user = request.user)
    if not admin.has_admin or not admin.dominates(user):
        print(admin.has_admin)
        print(admin.dominates(user))
        return JsonResponse({}, status = 403)
    user.security_level = int(data['level'])
    if('labels' not in data):
        data['labels'] = []
    user.security_labels = json.dumps(list(set(json.loads(user.security_labels) + data['labels'])))
    if not admin.dominates(user):
        return JsonResponse({}, status = 403)
    models.LogEntry.objects.create(actor = admin, action = "Changed Auth Level of %s To {%s, %s}"%(user, user.security_level, user.security_labels))
    user.save()
    return JsonResponse({}, status=200)

@csrf_exempt
@api_view(['POST','GET',"UPDATE",'PATCH'])
def template(request):  
    if not request.user.is_authenticated:
        return JsonResponse({}, status = 401)
    if request.method == 'POST':
        data = request.POST
        user = models.UserClass.objects.get(user = request.user)
        template = models.Template(name = data['name'], doc = request.FILES['doc'])
        if 'level' in data:
            template.security_level = data['level']
        if 'labels' in data:
            template.security_labels = data['labels']
        if 'category' in data:
            template.security_labels = data['category']
        if not template.dominates(user):
            return JsonResponse({'status':'violates Bell-La-Padula requirements'}, status = 403)
        template.save()
        models.LogEntry.objects.create(actor = user, action = "User %s created Template %s"%(user, template))
        return JsonResponse({'id':template.id}, status=201)
    if request.method == 'UPDATE':
        data = request.POST
        user = models.UserClass.objects.get(user = request.user)
        old_template = models.Template.objects.get(id=data['id'])
        template = models.Template(name = old_template.name, version = old_template.version + 1, doc = request.FILES['doc'], security_labels=old_template.security_labels, security_level=old_template.security_level)
        if not template.dominates(user):
            return JsonResponse({'status':'violates Bell-La-Padula requirements'}, status = 403)
        models.LogEntry.objects.create(actor = user, action = "User %s upgraded Template %s"%(user, template))
        template.save()
        return JsonResponse({'id':template.id}, status=201)
    if request.method == 'PATCH':
        data = json.loads(request.body)
        user = models.UserClass.objects.get(user = request.user)
        template = models.Template.objects.get(id=data['id'])
        if 'level' in data:
            template.security_level = data['level']
        if 'labels' in data:
            template.security_labels = data['labels']
        if 'category' in data:
            template.security_labels = data['category']
        if not template.dominates(user):
            return JsonResponse({'status':'violates Bell-La-Padula requirements'}, status = 403)
        if not template.dominates(old_template):
            return JsonResponse({'status':'violates Bell-La-Padula requirements'}, status = 403)
        template.save()
        models.LogEntry.objects.create(actor = user, action = "User %s changed version of Template %s"%(user, template))
        return JsonResponse({'id':template.id}, status=200)
    if request.method == 'GET':
        data = json.loads(request.body)
        template = models.Template.objects.get(id=data['id'])
        user = models.UserClass.objects.get(user = request.user)
        if not user.dominates(template):
            return JsonResponse({'status':'violates Bell-La-Padula requirements'}, status = 403)
        models.LogEntry.objects.create(actor = user, action = "User %s downloaded Template %s"%(user, template))
        return JsonResponse({'doc':template.doc.name, 'name':template.name}, status=200)

    return JsonResponse({}, status=405)

@csrf_exempt
@api_view(['POST'])
def auth_template(request):  
    if not request.user.is_authenticated:
        return JsonResponse({}, status = 401)
    data = json.loads(request.body)
    template = models.Template.objects.get(id=data['id'])
    user = models.UserClass.objects.get(user = request.user)
    if not user.has_admin or not user.dominates(template):
        return JsonResponse({'status':'violates Bell-La-Padula requirements'}, status = 403)
    template.authorised = not template.authorised
    template.save()
    models.LogEntry.objects.create(actor = user, action = "User %s changed auth status of Template %s"%(user, template))
    return JsonResponse({}, status = 200)

@csrf_exempt
@api_view(['POST'])
def get_template(request):  
    if not request.user.is_authenticated:
        return JsonResponse({}, status = 401)
    data = json.loads(request.body)
    user = models.UserClass.objects.get(user = request.user)
    if 'version' not in data:
        data['version'] = -1
    templates = sorted(models.Template.objects.filter(name = data['name'], version__gte = data['version']), key=lambda x: -x.version)
    for template in templates:
        if user.dominates(template):
            models.LogEntry.objects.create(actor = user, action = "User %s downloaded Template %s"%(user, template))
            return JsonResponse({'doc':template.doc.name, 'name':template.name, 'version':template.version, 'changed':template.version != int(data['version'])}, status=200)
    return JsonResponse({}, status = 404)