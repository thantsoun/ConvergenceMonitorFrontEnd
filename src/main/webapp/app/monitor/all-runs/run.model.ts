export class Run {
  constructor(
    public runIdRaw: string,
    public numIterationsRaw: string,
    public runId: string,
    public numIterations: string,
    public description: string,
    public startTime: string,
    public endTime: string
  ) {}
}
