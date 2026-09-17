export interface Incident {
  id: number
  serviceName: string
  environment: string
  title: string
  rawAlert: string
  status: 'PENDING' | 'ANALYZING' | 'COMPLETED' | 'FAILED'
  createdAt: string
  updatedAt: string
}

export interface AgentEvent {
  id: number
  incidentId: number
  eventType: string
  toolName: string | null
  content: string
  status: string
  createdAt: string
}

export interface KnowledgeReference {
  title: string
  source: string
  excerpt: string
}

export interface RcaReport {
  incidentSummary: string
  severity: string
  possibleRootCauses: Array<{ cause: string; confidence: number; reasoning: string }>
  evidence: Array<{ source: string; content: string; significance: string }>
  investigationSteps: Array<{ order: number; action: string; purpose: string }>
  relatedKnowledge: KnowledgeReference[]
}

export interface RcaReportResponse {
  incidentId: number
  modelName: string
  durationMs: number
  createdAt: string
  report: RcaReport
}

export interface Page<T> {
  content: T[]
  page: { totalElements: number }
}
