import axios from 'axios';

export function getErrorMessage(error: unknown) {
  if (axios.isAxiosError(error)) {
    const responseData = error.response?.data as Record<string, unknown> | undefined;
    const message = responseData?.message ?? responseData?.error;

    if (typeof message === 'string' && message.trim()) {
      return message;
    }

    return error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Unexpected error. Please try again.';
}
