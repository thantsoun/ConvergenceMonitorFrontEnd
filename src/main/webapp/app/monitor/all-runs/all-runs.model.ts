import { Run } from './run.model';

export class AllRuns {
  constructor(
    public runs: Run[],
    public query: string,
    public runIdHeader: string,
    public numIterationsHeader: string,
    public descriptionHeader: string,
    public startTimeHeader: string,
    public endTimeHeader: string
  ) {}
}
