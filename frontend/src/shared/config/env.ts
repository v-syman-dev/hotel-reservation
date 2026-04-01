const DEFAULT_API_BASE_URL = '/api';

function normalizeApiBaseUrl(value: string | undefined): string {
  const resolved = value?.trim() || DEFAULT_API_BASE_URL;

  if (resolved.length > 1 && resolved.endsWith('/')) {
    return resolved.slice(0, -1);
  }

  return resolved;
}

export const env = {
  apiBaseUrl: normalizeApiBaseUrl(import.meta.env.VITE_API_BASE_URL),
};

