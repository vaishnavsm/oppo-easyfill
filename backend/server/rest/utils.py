class AmountRecommendationEngine():
    def __init__(self, name):
        self.name = name
    def suggest(self, params):
        import random
        print(params)
        return random.randrange(params['min'], params['max'], 1)