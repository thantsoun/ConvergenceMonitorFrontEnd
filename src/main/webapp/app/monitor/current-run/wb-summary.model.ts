export class JobStatusSummary {
  constructor(public status: string, public color: string, public count: bigint, public percentage: number) {}
}

export class WbSummary {
  constructor(public timestamp: string, public iterIdDecoded: string, public totalCount: bigint, public jobs: JobStatusSummary[]) {}
}
