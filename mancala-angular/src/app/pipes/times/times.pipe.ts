import {PipeTransform, Pipe} from '@angular/core';

@Pipe({name: 'times'})
export class TimesPipe implements PipeTransform {
  transform(value: number, reverse?: boolean): any {
    const iterable = <Iterable<any>> {};
    if(reverse) {
      iterable[Symbol.iterator] = function* () {
        let n = value;
        while (n > 0) {
          yield --n;
        }
      };
    } else {
      iterable[Symbol.iterator] = function* () {
        let n = -1;
        while (n < value) {
          yield ++n;
        }
      };
    }
    return iterable;
  }
}