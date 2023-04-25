import './App.css'
import {useEffect, useState} from "react";
import ReactECharts from 'echarts-for-react';


const api = 'http://localhost:3000';

function App() {
    const [selectedSubKey, setSelectedSubKey] = useState<string>();
    const [subKeys, setSubKeys] = useState<string[]>([]);
    const [chartOptions, setChartOptions] = useState<any>();

    useEffect(() => {
        fetch(`${api}/subscribe-keys`)
            .then((res) => res.json())
            .then(setSubKeys)
    }, [])

    useEffect(() => {
        fetch(`${api}/channels?subscribeKey=${selectedSubKey}`)
            .then((res) => res.json())
            .then((data) => {
                const options = {
                    grid: { top: 8, right: 8, bottom: 24, left: 36 },
                    xAxis: {
                        type: 'category',
                        data: data.map((d) => d.channel),
                    },
                    yAxis: {
                        type: 'value',
                    },
                    series: [
                        {
                            data: data.map((d) => d.sum),
                            type: 'bar',
                            smooth: true,
                        },
                    ],
                    tooltip: {
                        trigger: 'axis',
                    },
                };
                setChartOptions(options)
            })
    }, [selectedSubKey])

    return (
        <>
            <h1>Hello Hackathon</h1>
            <select onChange={(e) => setSelectedSubKey(e.target.value)} value={selectedSubKey}>
                <option></option>
                {subKeys.map((subKey) => <option value={subKey}>{subKey}</option> )}
            </select>

            {chartOptions &&
                <div className="chart">
                    <ReactECharts option={chartOptions} />
                </div>
            }
        </>
    )
}

export default App
