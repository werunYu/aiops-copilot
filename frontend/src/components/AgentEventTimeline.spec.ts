import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import AgentEventTimeline from './AgentEventTimeline.vue'

describe('AgentEventTimeline', () => {
  it('renders the analysis-started event for an operator', () => {
    const wrapper = mount(AgentEventTimeline, {
      props: {
        events: [{
          id: 1,
          incidentId: 42,
          eventType: 'ANALYSIS_STARTED',
          toolName: null,
          content: '分析任务已开始',
          status: 'SUCCESS',
          createdAt: '2026-09-17 12:00:00',
        }],
      },
    })

    expect(wrapper.text()).toContain('分析开始')
    expect(wrapper.text()).toContain('分析任务已开始')
  })
})
