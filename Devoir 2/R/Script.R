sink('output1.txt')

mean <- 360
std <- sqrt(270)

z_score <- (((7 * 60) - mean) / std)

y_prob <- 1 - pnorm(z_score)

message <- paste("La probabilité que la durée totale des examens dépassent 7 heures est de ", (round(y_prob, 7) * 100), "%.")
print(message)

sink()

sink('output2.txt')

mean <- 360
std <- sqrt(750)

z_score <- (((7 * 60) - mean) / std)

y_prob <- 1 - pnorm(z_score)

message <- paste("La probabilité que la durée totale des examens dépassent 7 heures est de ", (round(y_prob, 5) * 100), "%.")
print(message)

sink()

sink('output3.txt')

lambda <- 35
total_prob <- 0

for (n in seq(0, 29)) {
  total_prob <- total_prob + dpois(n, lambda)
}

message <- paste("La probabilité que la durée totale des examens dépassent 7 heures est de ", (round(total_prob, 5) * 100), "%.")
print(message)

sink()
