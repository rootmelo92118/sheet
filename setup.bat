rm config.json web.config azureapp.exe geoip.dat geosite.dat geoip-only-cn-private.dat
curl -L https://github.com/XTLS/Xray-core/releases/latest/download/Xray-windows-32.zip >> Xray-windows-32.zip
unzip -o Xray-windows-32.zip
mv xray.exe azureapp.exe
rm Xray-windows-32.zip wxray.exe README.md LICENSE geoip.dat geosite.dat
curl -L https://github.com/rootmelo92118/sheet/raw/master/config.json >> config.json
curl -L https://github.com/rootmelo92118/sheet/raw/master/web.config >> web.config
curl -L https://github.com/Loyalsoldier/v2ray-rules-dat/releases/latest/download/rules.zip >> rules.zip
unzip rules.zip geoip.dat
unzip rules.zip geosite.dat
rm rules.zip
rm setup.bat && curl -L https://raw.githubusercontent.com/rootmelo92118/sheet/master/setup.bat >> setup.bat
