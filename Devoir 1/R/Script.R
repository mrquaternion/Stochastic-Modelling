sink("output1.txt")
library(ggplot2)

n <- 226
p <- 0.97
k <- 220

for (i in 1:6) {
  probX <- dbinom(k + i, size = n, prob = p)
  message <- paste("Probabilité que", i, "clients arrivent et n'ont pas de siège :", 
                   round(probX * 100, digits=2), "%")
  print(message)
}


probX0 <- pbinom(k, size=n, prob=p)
message <- paste("Probabilité qu'aucun client arrive et qu'il n'y a plus de siège : ", 
                      round(probX0 * 100, digits=2), "%")
print(message)
sink()

sink('output2.txt')
compute_cdf <- function(n, p, k) {
  pbinom(k, size=n, prob = p)
}
for (n in seq(226, 220, by = -1)) {
  prob <- compute_cdf(n, p, k)
  if (prob >= 0.9) {
    message <- paste("Le nombre maximal de billets à vendre est", n, "\n")
    print(message)
    break
  }
}
sink()

