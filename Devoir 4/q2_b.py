N = 5
probabilites = [(N - i) / N for i in range(1, N)]

somme_int = 0
for m in range(1, N): # s'arrête à N - 1
    produit_int = 1
    for j in range(1, m + 1): # s'arrête à m
        produit_int *= (1 - probabilites[j - 1]) / probabilites[j - 1]

    somme_int += produit_int

P_1 = 1 / (1 + somme_int)
print(f"La probabilité de se rendre à l'état N en débutant de l'état 1 est de {(round(P_1, 3) * 100)}%.")

for i in range(2, N):
    somme_int = 0
    for m in range(1, i): # s'arrête à i - 1
        produit_int = 1
        for j in range(1, m + 1):
            produit_int *= (1 - probabilites[j - 1]) / probabilites[j - 1]

        somme_int += produit_int

    P_i = P_1 * (1 + somme_int)
    print(f"La probabilité de se rendre à l'état N en débutant de l'état {i} est de {(round(P_i, 3) * 100)}%.")

