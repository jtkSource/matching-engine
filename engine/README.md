# Matching Core

Containss the core logic for matching trades in a order book

#### OrderBook

Matching always happens at the top most row where the bid and the ask is the closest


OrderBook{productId='XSS', priceType=Spread, reverseOrder=true}

 |                BID | ASK  |               
|:----------- :| :---------------------|
          5.00999999 | 4.33999999          
          5.03000000 | 4.33999999          
          5.33999999 | 4.03000000          
          5.33999999 | 4.00999999  

```
Execution :
newBid >= maxAsk
newAsk >= minBid
```

OrderBook{productId='XSS', priceType= , reverseOrder=false}

 |                BID | ASK  |               
|:----------- :| :---------------------|
         99.34000000 | 100.01000000        
         99.34000000 | 100.03000000        
         99.03000000 | 100.34000000        
         99.01000000 | 100.34000000        


```
Execution :
newBid <= minAsk
newAsk <= maxBid
```