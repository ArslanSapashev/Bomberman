# Bomberman
## Условия игры 
На игровом поле размещаются ячейки каждую из которых может занимать игрок, монстр или блок (ячейка недоступная для перехода).   
  
**Аргументы командной строки** определяют следующие параметры:  
1. количество рядов игрового поля (высота поля);  
2. количество столбцов игрового поля (ширина поля);  
3. количество монстров на игровом поле;  
4. период повтора (период времени в течении которого монстр повторяет попытки перейти на заданную ячейку, после истечения периода будет пытаться занять другую ячейку);  
5. количество блоков на игровом поле.  
  
**Логика движения монстров** в поисках Бомбермена основана на тепловых картах. У каждой ячейки присутствует поле degree. 
Чем дальше ячейка расположена от Бомбермена, тем выше ее "температура" (поле degree).
Данная величина определяется как максимальное из двух абсолютных значений разницы номера столбца и строки
каждой ячейки по отношению к ячейке в которой находится Бомбермен.
```
int degree = Math.max(
    Math.abs(cell.getRow() - player.getHostCell().getRow()),
    Math.abs(cell.getColumn() - player.getHostCell().getColumn())
);
```
Каждый из монстров стремится сделать свой следующий шаг в соседнюю ячейку с наименьшей температурой, таким образом приближаясь к Бомбермену.  
В любой момент времени каждая из ячеек (за исключением блоков) может находится в 3-х взаимоисключающих состояниях:  
1. занята Бомберменом  
2. занята монстром  
3. свободна  
Кем занята данная ячейка указано в поле actor класса Cell.
Во избежание накладных расходов на синхронизацию операций чтения/записи поля actor, **используются CAS-операции** из пакета java.util.concurrent.atomic.