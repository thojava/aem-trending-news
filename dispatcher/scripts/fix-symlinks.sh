echo FIXING SYMLINKS
# fix enabled_vhosts
rm /mnt/dev/src/conf.d/enabled_vhosts/default.vhost; ln -s /mnt/dev/src/conf.d/enabled_vhosts/../available_vhosts/default.vhost /mnt/dev/src/conf.d/enabled_vhosts/default.vhost

# fix enabled_farms
rm /mnt/dev/src/conf.dispatcher.d/enabled_farms/default.farm; ln -s /mnt/dev/src/conf.dispatcher.d/enabled_farms/../available_farms/default.farm /mnt/dev/src/conf.dispatcher.d/enabled_farms/default.farm