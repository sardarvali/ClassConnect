$p = [char]124
$files = Get-ChildItem -Path app\src\main\res\layout -Filter *.xml -Recurse
foreach ($f in $files) {
    if (Test-Path $f.FullName) {
        $content = [System.IO.File]::ReadAllText($f.FullName)
        $newContent = $content.Replace('topend', 'top' + $p + 'end').Replace('bottomend', 'bottom' + $p + 'end').Replace('topstart', 'top' + $p + 'start').Replace('bottomstart', 'bottom' + $p + 'start').Replace('bottomcenter_horizontal', 'bottom' + $p + 'center_horizontal')
        if ($content -ne $newContent) {
            [System.IO.File]::WriteAllText($f.FullName, $newContent, [System.Text.Encoding]::UTF8)
            Write-Host "Fixed " $f.Name
        }
    }
}
