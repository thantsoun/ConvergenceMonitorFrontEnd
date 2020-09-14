export const sort = (listOccurrences: any[], predicate: string, reverse: boolean): any[] => {
  if (listOccurrences == null) {
    return [];
  }
  listOccurrences.sort(function (a, b): number {
    let condition;
    if (isNaN(Number(a[predicate]))) {
      if (String(a[predicate]) === String(b[predicate])) {
        return 0;
      }
      condition = String(a[predicate]) > String(b[predicate]);
    } else {
      if (Number(a[predicate]) === Number(b[predicate])) {
        return 0;
      }
      condition = Number(a[predicate]) > Number(b[predicate]);
    }
    if (reverse) {
      condition = !condition;
    }
    if (condition) {
      return 1;
    } else if (false === condition) {
      return -1;
    }
    return 0;
  });
  return listOccurrences;
};
