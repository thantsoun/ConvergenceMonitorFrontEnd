export class JobStatusDetails {
  constructor(
    public id: bigint,
    public runId: bigint,
    public priority: bigint,
    public type: string,
    public creationTime: string,
    public status: string,
    public color: string,
    public startTime: string,
    public endTime: string,
    public duration: string,
    public worker: string,
    public message: string
  ) {}
}

export class WbDetails {
  constructor(public iterIdDecoded: string, public totalCount: bigint, public jobs: Map<string, JobStatusDetails[]>) {}
}
