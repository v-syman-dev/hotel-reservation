import type { Address } from '@/entities/address/model/types';

const moneyFormatter = new Intl.NumberFormat('ru-RU', {
  style: 'currency',
  currency: 'USD',
  maximumFractionDigits: 2,
});

const dateFormatter = new Intl.DateTimeFormat('ru-RU', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
});

export function formatMoney(value: number) {
  return moneyFormatter.format(value);
}

export function formatDate(value: string) {
  return dateFormatter.format(new Date(value));
}

export function formatAddress(address: Address) {
  return [address.country, address.city, address.street].filter(Boolean).join(', ');
}
