# Generated by Django 2.2.7 on 2019-12-31 13:10

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('rest', '0002_auto_20191231_1309'),
    ]

    operations = [
        migrations.AlterField(
            model_name='applicationfield',
            name='engine',
            field=models.TextField(blank=True, default='', null=True),
        ),
        migrations.AlterField(
            model_name='submission',
            name='data',
            field=models.TextField(),
        ),
    ]
