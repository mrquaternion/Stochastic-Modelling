import matplotlib.pyplot as plt
import numpy as np

X1 = []
Y1 = []
X2 = []
Y2 = []
with open('./Devoir 6/output1.txt') as file:
    for line in file:
        line_pieces = line.strip().split(",") 
        X1.append(float(line_pieces[0]))  
        Y1.append(float(line_pieces[1]))

with open('./Devoir 6/output2.txt') as file:
    for line in file:
        line_pieces = line.strip().split(",") 
        X2.append(float(line_pieces[0]))  
        Y2.append(float(line_pieces[1]))

max_freq_1 = max(Y1)
max_freq_2 = max(Y2)

fig, axs = plt.subplots(2, 1, figsize=(10, 10))
fig.suptitle(r'Distribution de lois de Poisson avec $\lambda = 25$', fontsize=16)

axs[0].plot(X1, Y1, marker='o')
axs[0].set_title('Méthode 1', fontsize=14)
axs[0].set_xlabel('Valeur X')
axs[0].set_ylabel('Fréquence Y')
axs[1].plot(X2, Y2, marker='o')
axs[1].set_title('Méthode 2', fontsize=14)
axs[1].set_xlabel('Valeur X')
axs[1].set_ylabel('Fréquence Y')

axs[0].axhline(y=max_freq_1, color='red', linestyle='--', label=f'Max: {int(max_freq_1)}')
axs[1].axhline(y=max_freq_2, color='red', linestyle='--', label=f'Max: {int(max_freq_2)}')

axs[0].legend()
axs[1].legend()

plt.tight_layout()
plt.show()
