#@user@ 远程登录用户，通常root
#@ip@ 远程服务器IP 例如：192.168.1.1
#@port@ 登录端口，默认22
#@cmd@ 需要执行的命令例如：cd /data/game/s1 && sh service.sh restart
ssh @user@@@ip@ -p @port@ "@cmd@"
