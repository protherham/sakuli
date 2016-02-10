# script for modify the user variables for starting Firefox correctly
# call the script via cmd `powershell -executionPolicy bypass -file "modfiy_firefox_variables.ps1" set`
# if no arguments are assigned, the script will delete SAKULI_HOME from the PATH

###### parsing arguments

param([string]$parSet = $null)
#set or not set

set-strictmode -version Latest

###### functions & parameters:
$keyFF = "MOZ_DISABLE_OOP_PLUGINS"
$keyFF2 = "MOZ_DISABLE_AUTO_SAFE_MODE"

function setUserVarToOne([string]$updateKey){
    [string] $newValue = 1
    echo "SET user environment variable '$updateKey': $newValue"
    Set-ItemProperty -Path 'Registry::HKEY_CURRENT_USER\Environment' -Name $updateKey -Value $newValue
}

function unsetUserVar([string]$keyValue) {
    $present = Get-ItemProperty -Path 'Registry::HKEY_CURRENT_USER\Environment' -Name "$keyValue" -ErrorAction SilentlyContinue
	if ($present){
		echo "CLEAR user environment vairable '$keyValue'"
    	Remove-ItemProperty -Path 'Registry::HKEY_CURRENT_USER\Environment' -Name "$keyValue"
	} else {
		echo "'$keyValue' is not set as environment variable, so nothing to do!"
	}
}

function notifyWindowsEnvironmentChange(){
	echo "Notify Windows about Environment Change!"

Add-Type -TypeDefinition @"
    using System;
    using System.Runtime.InteropServices;

    public class NativeMethods
    {
        [DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        public static extern IntPtr SendMessageTimeout(
            IntPtr hWnd, uint Msg, UIntPtr wParam, string lParam,
            uint fuFlags, uint uTimeout, out UIntPtr lpdwResult);
    }
"@

$HWND_BROADCAST = [IntPtr] 0xffff
$WM_SETTINGCHANGE = 0x1a
$SMTO_ABORTIFHUNG = 0x2
$result = [UIntPtr]::Zero

[void] ([Nativemethods]::SendMessageTimeout($HWND_BROADCAST, $WM_SETTINGCHANGE, [UIntPtr]::Zero, 'Environment', $SMTO_ABORTIFHUNG, 5000, [ref] $result))
}

###### excecution logic
	
if ($parSet.Equals("set")) {
	setUserVarToOne($keyFF)
	setUserVarToOne($keyFF2)
} else {
    unsetUserVar($keyFF)
    unsetUserVar($keyFF2)
}
notifyWindowsEnvironmentChange
echo "Environment update FINISHED!"
exit