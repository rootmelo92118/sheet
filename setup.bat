rm *
curl -L https://github.com/v2fly/v2ray-core/releases/latest/download/v2ray-windows-32.zip >> v2ray-windows-32.zip
unzip -f v2ray-windows-32.zip
mv v2ray.exe azureapp.exe
rm config.json
curl -L https://github.com/rootmelo92118/sheet/raw/master/config.json >> config.json
curl -L https://github.com/rootmelo92118/sheet/raw/master/web.config >> web.config
