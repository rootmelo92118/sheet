rm config.json web.config v2ctl.exe azureapp.exe geoip.dat geosite.dat geoip-only-cn-private.dat
curl -L https://github.com/v2fly/v2ray-core/releases/latest/download/v2ray-windows-32.zip >> v2ray-windows-32.zip
unzip -o v2ray-windows-32.zip
mv v2ray.exe azureapp.exe
rm config.json v2ray-windows-32.zip wv2ray.exe vpoint_socks_vmess.json vpoint_vmess_freedom.json geoip.dat geosite.dat
curl -L https://github.com/rootmelo92118/sheet/raw/master/config.json >> config.json
curl -L https://github.com/rootmelo92118/sheet/raw/master/web.config >> web.config
curl -L https://github.com/Loyalsoldier/v2ray-rules-dat/releases/latest/download/rules.zip >> rule.zip
unzip rules.zip geoip.dat
unzip rules.zip geosite.dat
rm rule.zip
