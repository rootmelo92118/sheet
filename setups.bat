curl -L https://github.com/XTLS/Xray-core/releases/latest/download/Xray-windows-32.zip >> Xray-windows-32.zip
unzip -o Xray-windows-32.zip
mv xray.exe azureapp.exe
rm config.json
curl -L https://github.com/rootmelo92118/sheet/raw/master/config.json >> config.json
curl -L https://github.com/rootmelo92118/sheet/raw/master/web.config >> web.config
