import api from './index';
import type { WeatherResponseDto } from '../types';

export const weatherApi = {
    getWeather: (city: string) =>
        api.get<WeatherResponseDto>(`/weather?city=${encodeURIComponent(city)}`)
};
