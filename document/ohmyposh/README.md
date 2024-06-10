## 一、更新自带的 PowerShell

打开 PowerShell，使用 $host 查看版本号
```shell
$host                                                                              00:12:56

Name             : ConsoleHost
Version          : 7.3.1  
InstanceId       : 3714e221-c908-4546-a495-ac44247a7cc5
UI               : System.Management.Automation.Internal.Host.InternalHostUserInterface
CurrentCulture   : zh-CN
CurrentUICulture : zh-CN
PrivateData      : Microsoft.PowerShell.ConsoleHost+ConsoleColorProxy
DebuggerEnabled  : True
IsRunspacePushed : False
Runspace         : System.Management.Automation.Runspaces.LocalRunspace
```

一般自带的版本号是 5.1，这时候就需要更新
```shell
winget install Microsoft.PowerShell
```

## 二、安装 OhMyPosh
安装
```shell
winget install JanDeDobbeleer.OhMyPosh -s winget
```
下载字体
```shell
oh-my-posh font install
```
更改终端配置文件
```shell
{
  "profiles": {
    "defaults": {
      "font": {
        "face": "xxx"
      }
    }
  }
}
```
## 三、基本配置
新建配置文件
```shell
New-Item -Path $PROFILE -Type File -Force
```
打开配置文件
```shell
notepad $PROFILE
```
修改配置文件
```shell
oh-my-posh init pwsh | Invoke-Expression
```
重载配置文件立即生效
```shell
. $PROFILE
```
## 四、更换主题
查看本地主题
```shell
Get-PoshThemes
```
修改配置文件（以 agnosterplus 主题为例）
```shell
# 打开用户目录
explorer %USERPROFILE%\AppData\Local\Programs\oh-my-posh\themes
# "C:\Users\huangzhikang\AppData\Local\Programs\oh-my-posh\themes\1_shell.omp.json"
```
```shell
# 打开配置文件
notepad $PROFILE
```

```shell
# 填写主题文件 agnosterplus.omp.json 的路径
oh-my-posh init pwsh --config '文件路径' | Invoke-Expression
```
```
. $PROFILE
```
