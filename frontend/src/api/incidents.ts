import { request } from './client'
import type { AgentEvent, Incident, Page, RcaReportResponse } from '../types/incident'

export const listIncidents = (page: number, pageSize: number) =>
  request<Page<Incident>>(`/api/incidents?page=${page}&pageSize=${pageSize}`)

export const getIncident = (id: number) => request<Incident>(`/api/incidents/${id}`)

export const createIncident = (incident: Pick<Incident, 'serviceName' | 'environment' | 'title' | 'rawAlert'>) =>
  request<Incident>('/api/incidents', { method: 'POST', body: JSON.stringify(incident) })

export const startAnalysis = (id: number) => request<{ incidentId: number; status: string }>(`/api/incidents/${id}/analyze`, { method: 'POST' })

export const getEvents = (id: number) => request<AgentEvent[]>(`/api/incidents/${id}/events`)

export const getReport = (id: number) => request<RcaReportResponse>(`/api/incidents/${id}/report`)

export function openIncidentStream(id: number, onEvent: (event: AgentEvent) => void): EventSource {
  const source = new EventSource(`/api/incidents/${id}/events/stream`)
  source.addEventListener('agent-event', (event) => onEvent(JSON.parse(event.data) as AgentEvent))
  return source
}
