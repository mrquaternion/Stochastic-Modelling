from itertools import product

G = {1: [2], 2: [3, 4], 3: [4], 4: [5], 5: []} # on pourrait faire G = {1: [2], 2: [1, 3, 4], 3: [2, 4], 4: [2, 3, 5], 5: [4]}
                                               # mais ça prendrait plus de temps parce qu'on visiterait un sommet adjacent déjà visité
                                               # dans depth_first_search()

def generate_configs(k: int, G: dict) -> list:
    colors = [i for i in range(1, k + 1)] # on crée une séquence de couleurs (pour k = 3 on a [1,2,3], pour k = 4 on a [1,2,3,4], etc)
    return list(product(colors, repeat=len(G))) # on génère toutes les permutations distingables | source : https://www.hackerrank.com/challenges/itertools-product/problem

def depth_first_search(config: tuple, G: dict) -> int:
    for i in range(len(config)):
        for v in G[i + 1]: # on regarde les sommets adjacents au sommet courant
            if config[v - 1] == config[i]: # si son sommet adjacent a la même couleur que lui, alors configuration non valide
                return 0
    return 1

ks = [3, 4, 5]
for k in ks:
    nb_configs = generate_configs(k, G)
    if len(nb_configs) == (k ** len(G)): # vérification qu'on a bien tous les configurations possibles
        nb_valid_config = 0
        for config in nb_configs:
            nb_valid_config += depth_first_search(config, G)
        print(f"Nombre de configurations admissibles pour un {k}-coloriage dans G : {nb_valid_config} sur {len(nb_configs)} configurations.")


