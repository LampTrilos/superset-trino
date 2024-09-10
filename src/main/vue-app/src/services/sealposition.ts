import {positionMap} from './models';

export function getPositionDescription(id: any): string | undefined {
   //console.log(id)
  //The pop-up edit table sends an object instead of just the string
  if (typeof id === 'object' && id !== null) {
    id = id.id
  }
  return positionMap.find(element => element.id === id).description || "Παρακαλώ συμπληρώστε";
}
