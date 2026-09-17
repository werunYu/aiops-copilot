export interface ApiResult<T> {
  code: string
  message: string
  data: T
}

export async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    headers: { 'Content-Type': 'application/json', ...(options?.headers ?? {}) },
    ...options,
  })
  const body = await response.json() as ApiResult<T>
  if (!response.ok || body.code !== '000000') {
    throw new Error(body.message || '请求失败')
  }
  return body.data
}
