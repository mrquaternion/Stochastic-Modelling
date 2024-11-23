import matplotlib.pyplot as plt
import numpy as np

# Initialisation des listes pour stocker les données
X = []
Y = []

# Lecture des données
with open("./Devoir 7/donnees.txt", 'r') as f:
    for line in f:
        values = line.strip().split(",")
        x_value = round(float(values[0]), 2)  # Arrondi à deux décimales
        y_value = float(values[1])  # Conversion en float

        if y_value < 10:
            X.append(1 / round(x_value, 2))
            Y.append(round(y_value, 5))

# Conversion en tableaux NumPy
X = np.array(X)
Y = np.array(Y)

# Ajustement d'une courbe polynomiale de degré 2
coefficients = np.polyfit(X, Y, 2)  # Ajustement quadratique
polynomial = np.poly1d(coefficients)

# Calcul des valeurs prédites
X_fit = np.linspace(min(X), max(X), 500)  # Plus de points pour une courbe lisse
Y_fit = polynomial(X_fit)

# Création du graphique
plt.plot(X, Y, 'o', label='Données', color='b')  # Points de données
plt.plot(X_fit, Y_fit, '-', label='Courbe ajustée', color='r')  # Courbe ajustée

# Ajout des étiquettes et du titre
plt.xlabel('lambda_0')
plt.ylabel('Var[X_is] / Var[X]')
plt.title('Optimisation de Lambda_0')
plt.legend()

# Affichage du graphique
plt.show()
