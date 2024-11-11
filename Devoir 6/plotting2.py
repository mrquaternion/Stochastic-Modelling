import matplotlib.pyplot as plt
import scipy.stats as stats

observations = []
with open('./Devoir 6/output3.txt') as file:
    for line in file:
        observations.append(int(line.strip()))

expected_observations = 1000

chi_squared = 0
for observation in observations:
    chi_squared += ((observation - expected_observations) ** 2) / expected_observations

print("Khi-carré :", chi_squared)

dof = len(observations) - 1
p_value = stats.chi2.sf(chi_squared, dof)

print("Valeur-p:", round(p_value, 4))

plt.figure(figsize=(12, 6))
plt.bar(range(216), observations, color='skyblue')
plt.axhline(y=expected_observations, color='r', linestyle='--', label='Nombre espéré d\'observations')
plt.xlabel('Coloriages admissibles')
plt.ylabel('Nombre d\'observations')
plt.title('Nombre d\'observations vs. nombre espéré pour chaque coloriage')
plt.legend()
plt.show()
